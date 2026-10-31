import { useEffect, useState } from 'react'
import { ArrowLeft, BookOpen, Save } from 'lucide-react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { createBook, getBook, getErrorMessage, updateBook } from '../services/LibraryService'
import { Loading, Notice, PageIntro } from './Ui'

const blankBook = { title: '', author: '', isbn: '', category: '', totalCopies: 1 }

export default function BookForm() {
  const { id } = useParams()
  const navigate = useNavigate()
  const editing = Boolean(id)
  const [form, setForm] = useState(blankBook)
  const [loading, setLoading] = useState(editing)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')

  useEffect(() => {
    if (!editing) return
    getBook(id).then((book) => setForm({ title: book.title, author: book.author, isbn: book.isbn, category: book.category, totalCopies: book.totalCopies }))
      .catch((err) => setError(getErrorMessage(err, 'Unable to load the book.'))).finally(() => setLoading(false))
  }, [editing, id])

  const updateField = (field, value) => setForm((current) => ({ ...current, [field]: field === 'totalCopies' ? Number(value) : value }))
  const handleSubmit = async (event) => {
    event.preventDefault(); setError('')
    if (!form.title.trim() || !form.author.trim() || !form.isbn.trim() || !form.category.trim() || form.totalCopies < 1) {
      setError('Complete all fields and use at least one total copy.'); return
    }
    setSaving(true)
    try {
      if (editing) await updateBook(id, form); else await createBook(form)
      navigate('/books', { state: { message: editing ? 'Book details updated successfully.' : 'Book added to the catalog.' } })
    } catch (err) { setError(getErrorMessage(err, 'The book could not be saved.')) } finally { setSaving(false) }
  }

  if (loading) return <><PageIntro eyebrow="Collection" title="Edit book" description="Update catalog details without losing circulation context." /><Loading label="Loading book details" /></>

  return <>
    <PageIntro eyebrow="Collection" title={editing ? 'Edit book' : 'Add book'} description={editing ? 'Update catalog details without losing circulation context.' : 'Add a title to the collection and set its copy count.'} action={<Link className="button button-secondary" to="/books"><ArrowLeft size={16} /> Back to books</Link>} />
    <Notice type="error" onClose={() => setError('')}>{error}</Notice>
    <section className="form-layout"><div className="panel form-panel"><div className="form-heading"><div className="form-icon"><BookOpen size={20} /></div><div><h2>{editing ? 'Catalog details' : 'New catalog entry'}</h2><p>Use the title page and library label to keep this record consistent.</p></div></div><form onSubmit={handleSubmit}><div className="form-grid"><label>Title<input value={form.title} onChange={(e) => updateField('title', e.target.value)} placeholder="e.g. Clean Code" required /></label><label>Author<input value={form.author} onChange={(e) => updateField('author', e.target.value)} placeholder="e.g. Robert C. Martin" required /></label><label>ISBN<input value={form.isbn} onChange={(e) => updateField('isbn', e.target.value)} placeholder="9780132350884" required /></label><label>Category<input value={form.category} onChange={(e) => updateField('category', e.target.value)} placeholder="e.g. Software" required /></label><label>Total copies<input type="number" min="1" value={form.totalCopies} onChange={(e) => updateField('totalCopies', e.target.value)} required /><span className="field-help">Available copies are calculated from active issues.</span></label></div><div className="form-actions"><Link className="button button-secondary" to="/books">Cancel</Link><button className="button button-primary" type="submit" disabled={saving}>{saving ? 'Saving…' : <><Save size={16} /> Save book</>}</button></div></form></div><aside className="form-aside"><div className="eyebrow">Good to know</div><h3>Catalog hygiene matters.</h3><p>ISBNs are unique, total copies cannot be less than one, and active issues are protected during edits.</p></aside></section>
  </>
}
