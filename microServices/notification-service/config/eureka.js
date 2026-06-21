// Configuration Eureka pour la découverte de services
// Enregistre le microservice NOTIFICATION-SERVICE auprès du serveur Eureka

const util = require('util');
const Eureka = require('eureka-js-client').Eureka;

/**
 * Initialise et configure le client Eureka
 * @param {number} port - Port du service
 * @returns {Eureka} Instance du client Eureka
 */
const initializeEureka = (port) => {
  const appName = process.env.SERVICE_NAME || 'NOTIFICATION-SERVICE';
  const hostName = process.env.HOSTNAME || 'localhost';
  const ipAddr = process.env.IP_ADDR || '127.0.0.1';
  const eurekaHost = process.env.EUREKA_HOST || 'localhost';
  const eurekaPort = process.env.EUREKA_PORT || 8761;

  const eureka = new Eureka({
    instance: {
      // Identifiant unique attendu par Eureka
      instanceId: `${appName}:${hostName}:${port}`,
      app: appName,
      hostName,
      ipAddr,
      vipAddress: appName,
      secureVipAddress: appName,
      port: {
  '$': 3000,
  '@enabled': true
},
      securePort: {
        '$': 443,
        '@enabled': 'false',
      },
      statusPageUrl: `http://${hostName}:${port}/health`,
      healthCheckUrl: `http://${hostName}:${port}/health`,
      homePageUrl: `http://${hostName}:${port}/`,
      dataCenterInfo: {
        '@class': 'com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo',
        name: 'MyOwn',
      },
      metadata: {
        'management.port': port,
        'service.type': 'notification',
      },
      status: 'UP',
    },

    eureka: {
      serviceUrls: {
        default: [`http://${eurekaHost}:${eurekaPort}/eureka/apps/`],
      },
      maxRetries: 3,
      requestRetryDelay: 1000,
      useDns: false,
    },

    // Optional: reduce noise, keep warnings and errors
    logger: {
      debug: () => {},
      info: () => {},
      warn: (msg) => console.log('⚠️  [Eureka]', msg),
      error: (msg) => console.log('❌ [Eureka]', msg),
    },
  });

  return eureka;
};

/**
 * Enregistre le service auprès d'Eureka avec retries
 * @param {Eureka} eureka - Client Eureka
 * @param {number} maxAttempts - Nombre de tentatives max (défaut 5)
 * @returns {Promise<void>}
 */
const registerService = async (eureka, maxAttempts = 5) => {
  let attempt = 1;

  const attemptRegistration = () => {
    return new Promise((resolve, reject) => {
      eureka.start((err) => {
        if (err) {
          const errorDetails = util.inspect(err, { depth: 5, colors: false });
          const responseBody = err && err.response ? err.response.body : undefined;
          const responseStatus = err && err.response ? err.response.statusCode : undefined;

          if (attempt < maxAttempts) {
            const delay = Math.pow(2, attempt) * 1000;
            console.log(`⏳ Eureka registration attempt ${attempt}/${maxAttempts} failed. Retry in ${delay}ms...`);
            console.error('❌ Eureka registration error details:', errorDetails);
            if (responseStatus || responseBody) {
              console.error('📡 Eureka response status:', responseStatus);
              console.error('📡 Eureka response body:', util.inspect(responseBody, { depth: 5, colors: false }));
            }
            setTimeout(() => {
              attempt++;
              attemptRegistration().then(resolve).catch(reject);
            }, delay);
          } else {
            console.error(`❌ Failed to register with Eureka after ${maxAttempts} attempts.`);
            console.error('❌ Final Eureka error details:', errorDetails);
            if (responseStatus || responseBody) {
              console.error('📡 Final Eureka response status:', responseStatus);
              console.error('📡 Final Eureka response body:', util.inspect(responseBody, { depth: 5, colors: false }));
            }
            reject(err);
          }
        } else {
          console.log('✅ Notification Service registered with Eureka');
          resolve();
        }
      });
    });
  };

  return attemptRegistration();
};

/**
 * Désenregistre le service auprès d'Eureka (graceful shutdown)
 * @param {Eureka} eureka - Client Eureka
 * @returns {Promise<void>}
 */
const deregisterService = async (eureka) => {
  return new Promise((resolve) => {
    eureka.stop(() => {
      console.log('✅ Notification Service deregistered from Eureka');
      resolve();
    });
  });
};

module.exports = {
  initializeEureka,
  registerService,
  deregisterService,
};
