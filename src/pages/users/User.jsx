import { useParams } from 'react-router-dom'
import { getUserById } from '../../api/users'

// main user page
export default function User() {
  const { userId } = useParams();
  const user = getUserById(userId);
  return (
    <div>{user}</div>
  )
}
