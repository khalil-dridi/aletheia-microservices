// Entrée principale du microservice
// Prépare l'application Express, la connexion MongoDB, Eureka et les routes
const express = require('express');
const morgan = require('morgan');
const cors = require('cors');
const dotenv = require('dotenv');
const util = require('util');
require('express-async-errors');

dotenv.config();

const { connectDB } = require('./config/db');
const { initializeEureka, registerService, deregisterService } = require('./config/eureka');
const notificationRoutes = require('./routes/notificationRoutes');
const { errorHandler, notFound } = require('./middlewares/errorHandler');

const app = express();
const PORT = process.env.PORT || 3000;

// Middlewares
app.use(express.json());
app.use(cors());
app.use(morgan('dev'));

// Routes
app.use('/notifications', notificationRoutes);

// Healthcheck
app.get('/health', (req, res) => res.status(200).json({ service: process.env.SERVICE_NAME || 'notification-service', status: 'ok' }));

// 404
app.use(notFound);

// Error handler
app.use(errorHandler);

// Start
const start = async () => {
  let eureka = null;
  let server = null;

  try {
    // 1. Connexion à MongoDB
    await connectDB(process.env.MONGO_URI);
    console.log('✅ MongoDB connected');

    // 2. Démarrer le serveur Express
    server = app.listen(PORT, () => {
      console.log(`✅ ${process.env.SERVICE_NAME || 'notification-service'} running on port ${PORT}`);
      console.log(`📍 Health check: http://127.0.0.1:${PORT}/health`);
    });

    // 3. Initialiser et enregistrer le service dans Eureka (en arrière-plan avec retries)
eureka = initializeEureka(PORT);    
    console.log(`🔗 Attempting Eureka registration at ${process.env.EUREKA_HOST}:${process.env.EUREKA_PORT}...`);
    
    // Lancer l'enregistrement sans bloquer le serveur
    registerService(eureka, 5)
      .then(() => {
        console.log('🎉 Service successfully registered with Eureka');
      })
      .catch((err) => {
        console.warn(
          `⚠️  Service running but Eureka registration failed after retries.\n` +
          `   Make sure Eureka Server is running at ${process.env.EUREKA_HOST}:${process.env.EUREKA_PORT}\n` +
          `   Error: ${err.message}`
        );
        console.warn('📌 Eureka registration error object:', util.inspect(err, { depth: 5, colors: false }));
      });

    // Graceful shutdown
    process.on('SIGTERM', async () => {
      console.log('📌 SIGTERM signal received: closing HTTP server');
      if (eureka) {
        await deregisterService(eureka).catch(() => {});
      }
      if (server) {
        server.close(() => {
          console.log('HTTP server closed');
          process.exit(0);
        });
      }
    });

    process.on('SIGINT', async () => {
      console.log('📌 SIGINT signal received: closing HTTP server');
      if (eureka) {
        await deregisterService(eureka).catch(() => {});
      }
      if (server) {
        server.close(() => {
          console.log('HTTP server closed');
          process.exit(0);
        });
      }
    });
  } catch (err) {
    console.error('❌ Failed to start server:', err.message);
    if (eureka) {
      await deregisterService(eureka).catch(() => {});
    }
    process.exit(1);
  }
};

start();
