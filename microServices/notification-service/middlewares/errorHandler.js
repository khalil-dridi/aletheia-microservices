// Middlewares de gestion des erreurs et 404
const notFound = (req, res, next) => {
  res.status(404).json({ error: 'Not Found' });
};

const errorHandler = (err, req, res, next) => {
  console.error(err);
  const status = err.status || 500;
  const message = err.message || 'Internal Server Error';
  res.status(status).json({ error: message });
};

module.exports = { notFound, errorHandler };
