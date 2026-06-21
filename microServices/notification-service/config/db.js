// Connexion à MongoDB via mongoose
const mongoose = require('mongoose');

const connectDB = async (mongoUri) => {
  if (!mongoUri) throw new Error('MONGO_URI non défini');
  try {
    await mongoose.connect(mongoUri, {
      // options modern mongoose no longer require many options
    });
    console.log('MongoDB connected');
  } catch (err) {
    console.error('MongoDB connection error:', err.message);
    throw err;
  }
};

module.exports = { connectDB };
