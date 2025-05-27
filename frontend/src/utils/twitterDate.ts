export default function convertToTwitterDate(timestamp: Date) {
  // Convert timestamp to Date object
  // Calculate the time difference
  const now = new Date()
  const diff = now.getTime() - timestamp.getTime()

  // Format the date based on the time difference
  if (diff < 60000) {
    // less than 1 minute
    return 'just now'
  } else if (diff < 3600000) {
    // less than 1 hour
    return `${Math.floor(diff / 60000)} minutes ago`
  } else if (diff < 86400000) {
    // less than 1 day
    return `${Math.floor(diff / 3600000)} hours ago`
  } else if (diff < 604800000) {
    // less than 1 week
    return `${Math.floor(diff / 86400000)} days ago`
  } else {
    return timestamp.toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric',
      year: 'numeric',
    })
  }
}
