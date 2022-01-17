import { useParams } from 'react-router-dom'
import { useUser } from '../../hooks/users'

// main user page
export default function User() {
  const params = useParams()
  const { user, isLoading } = useUser(params.userId)


  if (isLoading) return <div>loading</div>

  return (
    <div>Viewing user {user.name}</div>
  )
}
