import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' },
})

const unwrap = (request) => request.then((response) => response.data)

export const getBooks = (params = {}) => unwrap(api.get('/books', { params }))
export const getBook = (id) => unwrap(api.get(`/books/${id}`))
export const createBook = (payload) => unwrap(api.post('/books', payload))
export const updateBook = (id, payload) => unwrap(api.put(`/books/${id}`, payload))
export const deleteBook = (id) => unwrap(api.delete(`/books/${id}`))

export const getMembers = (params = {}) => unwrap(api.get('/members', { params }))
export const getMember = (id) => unwrap(api.get(`/members/${id}`))
export const createMember = (payload) => unwrap(api.post('/members', payload))
export const updateMember = (id, payload) => unwrap(api.put(`/members/${id}`, payload))
export const deleteMember = (id) => unwrap(api.delete(`/members/${id}`))

export const getTransactions = (params = {}) => unwrap(api.get('/transactions', { params }))
export const getDashboard = () => unwrap(api.get('/transactions/dashboard'))
export const getFineRate = () => unwrap(api.get('/transactions/fine-rate'))
export const issueBook = (payload) => unwrap(api.post('/transactions/issue', payload))
export const returnBook = (id) => unwrap(api.post(`/transactions/${id}/return`))

export const getErrorMessage = (error, fallback = 'Something went wrong. Please try again.') => {
  const data = error?.response?.data
  if (data?.fields) return Object.values(data.fields).join(' ')
  return data?.message || error?.message || fallback
}
