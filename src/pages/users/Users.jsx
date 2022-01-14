import { Link } from 'react-router-dom'
import { getUsers } from '../../api/users'

import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemText from '@mui/material/ListItemText';

export default function Users() {
  const users = getUsers();

  return (
    <List sx={{ width: '100%', maxWidth: 360, bgcolor: 'background.paper' }}>
      {users.map(({ name }) => (
        <ListItem key={name}>
          <Link
            style={{ display: "block", margin: "1rem 0" }}
            to={`/users/${name}`}
          >
            <ListItemText>{name}</ListItemText>
          </Link>
        </ListItem>
      ))}
    </List>
  )
}
