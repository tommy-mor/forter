import { Link } from 'react-router-dom'
import { useTags } from '../../hooks/tags'

import Box from '@mui/material/Box'
import Typography from '@mui/material/Typography'

export default function Tags() {
  const { tags, isLoading } = useTags()

  if (isLoading) return <div>loading</div>

  return (
    <Box>
      {tags.map((tag) => (
        <Link
          style={{ display: "block", margin: "1rem 0" }}
          to={`/tags/${tag.id}`}
          key={tag.id}
        >
          <Typography>{tag.name}</Typography>
        </Link>
      ))}
    </Box>
  )
}
