import { useParams } from 'react-router-dom'
import { getUserById } from '../../api/users'

// main user page
export default function User() {
  const params = useParams()
  const user = getUserById(params.userId)
  return (
    <div>Viewing user {user.name}</div>
  )
}
