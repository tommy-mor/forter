import { Link } from 'react-router-dom'
import { useUsers } from '../../hooks/users'

import Box from '@mui/material/Box'
import Typography from '@mui/material/Typography'

export default function Users() {
  const { users, isLoading } = useUsers()

  if (isLoading) return <div>loading</div>

  return (
      <Box>
      {users.map(({ name }) => (
          <Link
            style={{ display: "block", margin: "1rem 0" }}
            to={`/users/${name}`}
            key={name}
          >
            <Typography>{name}</Typography>
          </Link>
      ))}
    </Box>
  )
}
