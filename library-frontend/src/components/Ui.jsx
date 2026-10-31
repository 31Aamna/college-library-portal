import { AlertCircle, CheckCircle2, LoaderCircle, SearchX } from 'lucide-react'

export function Loading({ label = 'Loading records' }) {
  return <div className="loading-state"><LoaderCircle size={18} className="spin" /> {label}</div>
}

export function Notice({ type = 'error', children, onClose }) {
  if (!children) return null
  const Icon = type === 'success' ? CheckCircle2 : AlertCircle
  return <div className={`notice notice-${type}`} role="status">
    <Icon size={17} />
    <span>{children}</span>
    {onClose && <button className="notice-close" onClick={onClose} aria-label="Dismiss">×</button>}
  </div>
}

export function EmptyState({ title = 'No records found', description = 'Try changing your search or add a new record.' }) {
  return <div className="empty-state"><SearchX size={24} /><strong>{title}</strong><span>{description}</span></div>
}

export function PageIntro({ eyebrow, title, description, action }) {
  return <div className="page-intro">
    <div><div className="eyebrow">{eyebrow}</div><h1>{title}</h1><p>{description}</p></div>
    {action && <div className="page-intro-action">{action}</div>}
  </div>
}

export function formatDate(value) {
  if (!value) return '—'
  return new Intl.DateTimeFormat('en', { day: '2-digit', month: 'short', year: 'numeric' }).format(new Date(`${value}T00:00:00`))
}

export function statusLabel(status, overdue = false) {
  if (overdue) return 'Overdue'
  return status === 'ISSUED' ? 'Issued' : 'Returned'
}
