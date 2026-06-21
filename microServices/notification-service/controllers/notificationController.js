// Contrôleur pour gérer les notifications
const Joi = require('joi');
const Notification = require('../models/Notification');

// Validation minimale pour la création
const createSchema = Joi.object({
  userId: Joi.string().required(),
  message: Joi.string().min(1).required()
});

// POST /notifications
const createNotification = async (req, res, next) => {
  try {
    const { error, value } = createSchema.validate(req.body);
    if (error) return res.status(400).json({ error: error.details[0].message });

    const notification = new Notification(value);
    const saved = await notification.save();

    // In future: publish to RabbitMQ event here

    return res.status(201).json(saved);
  } catch (err) {
    next(err);
  }
};

// GET /notifications
const getAllNotifications = async (req, res, next) => {
  try {
    const notifications = await Notification.find().sort({ createdAt: -1 });
    return res.json(notifications);
  } catch (err) {
    next(err);
  }
};

// GET /notifications/user/:userId
const getUserNotifications = async (req, res, next) => {
  try {
    const { userId } = req.params;
    const notifications = await Notification.find({ userId }).sort({ createdAt: -1 });
    return res.json(notifications);
  } catch (err) {
    next(err);
  }
};

// PUT /notifications/:id/read
const markAsRead = async (req, res, next) => {
  try {
    const { id } = req.params;
    const notification = await Notification.findById(id);
    if (!notification) return res.status(404).json({ error: 'Notification non trouvée' });
    notification.read = true;
    await notification.save();
    return res.json(notification);
  } catch (err) {
    next(err);
  }
};

// DELETE /notifications/:id
const deleteNotification = async (req, res, next) => {
  try {
    const { id } = req.params;
    const result = await Notification.findByIdAndDelete(id);
    if (!result) return res.status(404).json({ error: 'Notification non trouvée' });
    return res.status(204).send();
  } catch (err) {
    next(err);
  }
};

module.exports = {
  createNotification,
  getAllNotifications,
  getUserNotifications,
  markAsRead,
  deleteNotification
};
