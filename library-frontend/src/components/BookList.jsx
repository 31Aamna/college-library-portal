import { useEffect, useMemo, useState } from 'react'
import { Edit3, Plus, Search, Trash2 } from 'lucide-react'
import { Link, useLocation } from 'react-router-dom'
import { deleteBook, getBooks, getErrorMessage } from '../services/LibraryService'
import { EmptyState, Loading, Notice, PageIntro } from './Ui'

export default function BookList() {
  const location = useLocation()
  const [books, setBooks] = useState([])
  const [search, setSearch] = useState('')
  const [category, setCategory] = useState('')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState(location.state?.message || '')

  const categories = useMemo(() => [...new Set(books.map((book) => book.category))].sort(), [books])
  const loadBooks = () => {
    setLoading(true)
    getBooks({ search: search || undefined, category: category || undefined })
      .then(setBooks).catch((err) => setError(getErrorMessage(err, 'Unable to load books.'))).finally(() => setLoading(false))
  }
  useEffect(() => { loadBooks() }, [search, category])

  const handleDelete = async (book) => {
    if (!window.confirm(`Delete “${book.title}” from the catalog?`)) return
    try { await deleteBook(book.id); setSuccess('Book deleted successfully.'); loadBooks() }
    catch (err) { setError(getErrorMessage(err, 'The book could not be deleted.')) }
  }

  return <>
    <PageIntro eyebrow="Collection" title="Books" description="Manage titles, copy counts, and catalog details." action={<Link className="button button-primary" to="/books/new"><Plus size={17} /> Add book</Link>} />
    <Notice type="success" onClose={() => setSuccess('')}>{success}</Notice>
    <Notice type="error" onClose={() => setError('')}>{error}</Notice>
    <section className="panel">
      <div className="toolbar"><div className="search-field"><Search size={17} /><input value={search} onChange={(event) => setSearch(event.target.value)} placeholder="Search title, author, ISBN, or category" aria-label="Search books" /></div><select className="filter-select" value={category} onChange={(event) => setCategory(event.target.value)} aria-label="Filter by category"><option value="">All categories</option>{categories.map((item) => <option key={item} value={item}>{item}</option>)}</select><span className="result-count">{books.length} {books.length === 1 ? 'title' : 'titles'}</span></div>
      {loading ? <Loading label="Loading catalog" /> : books.length === 0 ? <EmptyState title="No books match this view" description="Try another search or add a new title to the catalog." /> : <div className="table-scroll"><table><thead><tr><th>Book</th><th>ISBN</th><th>Category</th><th>Copies</th><th>Availability</th><th className="actions-col">Actions</th></tr></thead><tbody>{books.map((book) => <tr key={book.id}><td><strong>{book.title}</strong><span className="cell-muted">{book.author}</span></td><td className="mono-text">{book.isbn}</td><td><span className="category-chip">{book.category}</span></td><td>{book.totalCopies}</td><td><div className="availability"><strong>{book.availableCopies}</strong><span>of {book.totalCopies}</span></div></td><td className="actions-col"><div className="row-actions"><Link className="icon-button" to={`/books/${book.id}/edit`} title="Edit book" aria-label={`Edit ${book.title}`}><Edit3 size={16} /></Link><button className="icon-button danger" onClick={() => handleDelete(book)} title="Delete book" aria-label={`Delete ${book.title}`}><Trash2 size={16} /></button></div></td></tr>)}</tbody></table></div>}
    </section>
  </>
}
