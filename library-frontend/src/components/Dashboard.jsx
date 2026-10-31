import { useEffect, useMemo, useState } from 'react'
import { ArrowUpRight, BookMarked, BookOpen, Clock3, UsersRound } from 'lucide-react'
import { getBooks, getDashboard, getErrorMessage, getMembers, getTransactions } from '../services/LibraryService'
import { EmptyState, Loading, Notice, PageIntro, formatDate, statusLabel } from './Ui'
import { Link } from 'react-router-dom'

function StatCard({ label, value, hint, icon: Icon, tone = 'navy' }) {
  return <div className={`stat-card stat-${tone}`}>
    <div className="stat-card-top"><span>{label}</span><div className="stat-icon"><Icon size={18} /></div></div>
    <strong>{value}</strong><small>{hint}</small>
  </div>
}

export default function Dashboard() {
  const [dashboard, setDashboard] = useState(null)
  const [transactions, setTransactions] = useState([])
  const [books, setBooks] = useState([])
  const [members, setMembers] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    Promise.all([getDashboard(), getTransactions(), getBooks(), getMembers()])
      .then(([summary, history, bookRows, memberRows]) => {
        setDashboard(summary); setTransactions(history); setBooks(bookRows); setMembers(memberRows)
      })
      .catch((err) => setError(getErrorMessage(err, 'Unable to load dashboard data.')))
      .finally(() => setLoading(false))
  }, [])

  const categories = useMemo(() => new Set(books.map((book) => book.category)).size, [books])
  if (loading) return <><PageIntro eyebrow="Library overview" title="Keep the collection moving." description="A quick view of collection health and daily circulation." /><Loading label="Loading library overview" /></>

  return <>
    <PageIntro eyebrow="Library overview" title="Keep the collection moving." description="A quick view of collection health and daily circulation." action={<Link className="button button-primary" to="/issue-return"><ArrowUpRight size={17} /> Open circulation</Link>} />
    <Notice type="error" onClose={() => setError('')}>{error}</Notice>
    <section className="stat-grid">
      <StatCard label="Total books" value={dashboard?.totalBooks ?? 0} hint={`${categories} categories in catalog`} icon={BookOpen} />
      <StatCard label="Available copies" value={dashboard?.availableCopies ?? 0} hint="Ready to issue today" icon={BookMarked} tone="teal" />
      <StatCard label="Registered members" value={dashboard?.registeredMembers ?? 0} hint="Active library identities" icon={UsersRound} tone="slate" />
      <StatCard label="Issued books" value={dashboard?.issuedBooks ?? 0} hint="Currently in circulation" icon={Clock3} tone={dashboard?.issuedBooks ? 'amber' : 'teal'} />
      <StatCard label="Overdue books" value={dashboard?.overdueBooks ?? 0} hint={dashboard?.overdueBooks ? 'Follow up required' : 'No overdue items'} icon={Clock3} tone={dashboard?.overdueBooks ? 'red' : 'teal'} />
    </section>

    <section className="dashboard-grid">
      <div className="panel activity-panel">
        <div className="panel-heading"><div><div className="eyebrow">Latest movement</div><h2>Recent activity</h2></div><Link className="text-link" to="/transactions">View history <ArrowUpRight size={15} /></Link></div>
        {transactions.length === 0 ? <EmptyState title="No activity yet" description="Issued and returned books will appear here." /> : <div className="table-scroll"><table><thead><tr><th>Book</th><th>Member</th><th>Activity</th><th>Date</th></tr></thead><tbody>{transactions.slice(0, 6).map((transaction) => <tr key={transaction.id}><td><strong>{transaction.bookTitle}</strong><span className="cell-muted">Due {formatDate(transaction.dueDate)}</span></td><td><span>{transaction.memberName}</span><span className="cell-muted">{transaction.memberCode}</span></td><td><span className={`status-badge status-${transaction.overdue ? 'overdue' : transaction.status.toLowerCase()}`}>{statusLabel(transaction.status, transaction.overdue)}</span></td><td>{formatDate(transaction.returnDate || transaction.issueDate)}</td></tr>)}</tbody></table></div>}
      </div>
      <div className="panel quick-panel">
        <div className="panel-heading"><div><div className="eyebrow">At a glance</div><h2>Desk notes</h2></div></div>
        <div className="note-list">
          <div className="desk-note"><span className="note-index">01</span><div><strong>Collection health</strong><p>{dashboard?.availableCopies ?? 0} copies are available across {dashboard?.totalBooks ?? 0} titles.</p></div></div>
          <div className="desk-note"><span className="note-index">02</span><div><strong>Member service</strong><p>{dashboard?.registeredMembers ?? 0} registered members can borrow from the desk.</p></div></div>
          <div className="desk-note"><span className="note-index">03</span><div><strong>Follow-up queue</strong><p>{dashboard?.overdueBooks ? `${dashboard.overdueBooks} overdue item${dashboard.overdueBooks === 1 ? '' : 's'} need attention.` : 'The overdue queue is clear for now.'}</p></div></div>
        </div>
        <div className="mini-links"><Link to="/books">Manage catalog <ArrowUpRight size={15} /></Link><Link to="/members">Review members <ArrowUpRight size={15} /></Link></div>
      </div>
    </section>
  </>
}
