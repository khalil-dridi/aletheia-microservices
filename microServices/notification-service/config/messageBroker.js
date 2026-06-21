// Placeholder pour intégration future avec RabbitMQ
// On préparera ici la connexion et la publication/souscription
module.exports = {
  connect: async () => {
    // TODO: ajouter la connexion à RabbitMQ (amqplib ou rascal)
  },
  publish: async (queue, message) => {
    // TODO: implémenter publication
  },
  consume: async (queue, handler) => {
    // TODO: implémenter consommation
  }
};
