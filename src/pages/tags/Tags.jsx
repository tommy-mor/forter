import { Link } from 'react-router-dom'
import { useTags } from '../../hooks/tags'

export default function Tags() {
  const { tags, isLoading } = useTags()

  if (isLoading) return <div>loading</div>

  return (
    <div>
      {tags.map((tag) => (
        <Link
          style={{ display: "block", margin: "1rem 0" }}
          to={`/tags/${tag.id}`}
          key={tag.id}
        >
          {tag.name}
        </Link>
      ))}
    </div>
  )
}
