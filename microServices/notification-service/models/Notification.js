const mongoose = require('mongoose');

const NotificationSchema = new mongoose.Schema({
  userId: {
    type: String,
    required: true,
    index: true
  },
  message: {
    type: String,
    required: true
  },
  read: {
    type: Boolean,
    default: false
  }
}, {
  timestamps: { createdAt: 'createdAt', updatedAt: false }
});

// Virtual id to expose `id` instead of `_id` when serialized
NotificationSchema.virtual('id').get(function () {
  return this._id.toHexString();
});

NotificationSchema.set('toJSON', { virtuals: true });

module.exports = mongoose.model('Notification', NotificationSchema);
