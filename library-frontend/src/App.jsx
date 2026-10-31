import { useState } from 'react'
import { NavLink, Outlet, Route, Routes, useLocation } from 'react-router-dom'
import {
  ArrowLeftRight, BookCopy, BookOpen, ChevronRight, CircleHelp, LayoutDashboard,
  LibraryBig, Menu, PanelLeftClose, PanelLeftOpen, ReceiptText, Users, X
} from 'lucide-react'
import Dashboard from './components/Dashboard'
import BookList from './components/BookList'
import BookForm from './components/BookForm'
import MemberList from './components/MemberList'
import MemberForm from './components/MemberForm'
import IssueReturn from './components/IssueReturn'
import TransactionHistory from './components/TransactionHistory'

const navItems = [
  { to: '/', label: 'Dashboard', icon: LayoutDashboard, end: true },
  { to: '/books', label: 'Books', icon: BookOpen },
  { to: '/members', label: 'Members', icon: Users },
  { to: '/issue-return', label: 'Issue & Return', icon: ArrowLeftRight },
  { to: '/transactions', label: 'Transaction History', icon: ReceiptText },
]

const pageMeta = {
  '/': ['Dashboard', 'A quick view of collection health and daily circulation.'],
  '/books': ['Books', 'Manage the collection, availability, and catalog details.'],
  '/books/new': ['Add book', 'Add a title to the collection and set its copy count.'],
  '/members': ['Members', 'Keep member records accurate and ready for circulation.'],
  '/members/new': ['Register member', 'Create a library identity for a student or staff member.'],
  '/issue-return': ['Issue & Return', 'Move books through circulation with clear due dates.'],
  '/transactions': ['Transaction history', 'Review issued, returned, and overdue circulation records.'],
}

function Layout() {
  const location = useLocation()
  const [mobileOpen, setMobileOpen] = useState(false)
  const [collapsed, setCollapsed] = useState(false)
  const meta = pageMeta[location.pathname] || (location.pathname.includes('/edit')
    ? [location.pathname.includes('/books') ? 'Edit book' : 'Edit member', 'Update details without losing circulation context.']
    : ['Library workspace', 'Keep the collection moving.'])

  return <div className={`app-shell ${collapsed ? 'sidebar-collapsed' : ''}`}>
    <aside className={`sidebar ${mobileOpen ? 'sidebar-open' : ''}`}>
      <div className="brand-lockup">
        <div className="brand-mark"><BookCopy size={22} strokeWidth={1.8} /></div>
        <div className="brand-copy"><strong>COLLEGE</strong><span>LIBRARY</span></div>
        <button className="mobile-close" onClick={() => setMobileOpen(false)} aria-label="Close navigation"><X size={19} /></button>
      </div>
      <div className="sidebar-section-label">Workspace</div>
      <nav className="nav-list">
        {navItems.map(({ to, label, icon: Icon, end }) => <NavLink key={to} to={to} end={end} onClick={() => setMobileOpen(false)} className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}>
          <Icon size={18} strokeWidth={1.9} /><span>{label}</span>
        </NavLink>)}
      </nav>
      <div className="sidebar-footer">
        <div className="support-card"><CircleHelp size={17} /><div><strong>Need a hand?</strong><span>Check the viva guide in docs.</span></div></div>
        <div className="sidebar-user"><div className="avatar">LB</div><div><strong>Library desk</strong><span>Administration</span></div></div>
      </div>
    </aside>
    {mobileOpen && <button className="mobile-scrim" onClick={() => setMobileOpen(false)} aria-label="Close navigation" />}
    <main className="main-content">
      <header className="topbar">
        <button className="mobile-menu" onClick={() => setMobileOpen(true)} aria-label="Open navigation"><Menu size={21} /></button>
        <div className="breadcrumb"><LibraryBig size={16} /><ChevronRight size={14} /><span>{meta[0]}</span></div>
        <div className="topbar-actions"><span className="system-status"><span className="status-dot" /> System online</span><button className="collapse-toggle" onClick={() => setCollapsed((value) => !value)} aria-label="Toggle sidebar">{collapsed ? <PanelLeftOpen size={18} /> : <PanelLeftClose size={18} />}</button></div>
      </header>
      <div className="content-wrap"><Outlet /></div>
    </main>
  </div>
}

function NotFound() {
  return <div className="not-found"><BookOpen size={28} /><h1>Page not found</h1><p>The library route you requested does not exist.</p><NavLink className="button button-primary" to="/">Back to dashboard</NavLink></div>
}

export default function App() {
  return <Routes>
    <Route element={<Layout />}>
      <Route path="/" element={<Dashboard />} />
      <Route path="/books" element={<BookList />} />
      <Route path="/books/new" element={<BookForm />} />
      <Route path="/books/:id/edit" element={<BookForm />} />
      <Route path="/members" element={<MemberList />} />
      <Route path="/members/new" element={<MemberForm />} />
      <Route path="/members/:id/edit" element={<MemberForm />} />
      <Route path="/issue-return" element={<IssueReturn />} />
      <Route path="/transactions" element={<TransactionHistory />} />
      <Route path="*" element={<NotFound />} />
    </Route>
  </Routes>
}
