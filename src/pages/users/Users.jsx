import { Link } from 'react-router-dom'
import { getUsers } from '../../api/users'

export default function Users() {
  const users = getUsers();
  return <div>{users.map((user) => (
    <Link
      style={{ display: "block", margin: "1rem 0" }}
      to={`/users/${user.name}`}
      key={user.name}
    >
      {user.name}
    </Link>
  ))
  }</div>
}
