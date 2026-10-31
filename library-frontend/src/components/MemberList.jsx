import { useEffect, useState } from 'react'
import { Edit3, Mail, Plus, Search, Trash2 } from 'lucide-react'
import { Link, useLocation } from 'react-router-dom'
import { deleteMember, getErrorMessage, getMembers } from '../services/LibraryService'
import { EmptyState, Loading, Notice, PageIntro, formatDate } from './Ui'

export default function MemberList() {
  const location = useLocation()
  const [members, setMembers] = useState([])
  const [search, setSearch] = useState('')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState(location.state?.message || '')
  const loadMembers = () => { setLoading(true); getMembers({ search: search || undefined }).then(setMembers).catch((err) => setError(getErrorMessage(err, 'Unable to load members.'))).finally(() => setLoading(false)) }
  useEffect(() => { loadMembers() }, [search])
  const handleDelete = async (member) => { if (!window.confirm(`Delete member ${member.memberCode} — ${member.name}?`)) return; try { await deleteMember(member.id); setSuccess('Member deleted successfully.'); loadMembers() } catch (err) { setError(getErrorMessage(err, 'The member could not be deleted.')) } }

  return <>
    <PageIntro eyebrow="Member service" title="Members" description="Keep member records accurate and ready for circulation." action={<Link className="button button-primary" to="/members/new"><Plus size={17} /> Register member</Link>} />
    <Notice type="success" onClose={() => setSuccess('')}>{success}</Notice><Notice type="error" onClose={() => setError('')}>{error}</Notice>
    <section className="panel"><div className="toolbar"><div className="search-field"><Search size={17} /><input value={search} onChange={(event) => setSearch(event.target.value)} placeholder="Search member ID, name, or email" aria-label="Search members" /></div><span className="result-count">{members.length} {members.length === 1 ? 'member' : 'members'}</span></div>{loading ? <Loading label="Loading member directory" /> : members.length === 0 ? <EmptyState title="No members match this view" description="Try another search or register a new member." /> : <div className="table-scroll"><table><thead><tr><th>Member</th><th>Contact</th><th>Phone</th><th>Registered</th><th className="actions-col">Actions</th></tr></thead><tbody>{members.map((member) => <tr key={member.id}><td><div className="member-cell"><div className="avatar avatar-small">{member.name.split(' ').map((part) => part[0]).slice(0, 2).join('')}</div><div><strong>{member.name}</strong><span className="cell-muted">{member.memberCode}</span></div></div></td><td><span className="email-cell"><Mail size={14} />{member.email}</span></td><td>{member.phone}</td><td>{formatDate(member.registrationDate)}</td><td className="actions-col"><div className="row-actions"><Link className="icon-button" to={`/members/${member.id}/edit`} title="Edit member" aria-label={`Edit ${member.name}`}><Edit3 size={16} /></Link><button className="icon-button danger" onClick={() => handleDelete(member)} title="Delete member" aria-label={`Delete ${member.name}`}><Trash2 size={16} /></button></div></td></tr>)}</tbody></table></div>}</section>
  </>
}
