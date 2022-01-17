import { useParams, Link as LocalLink } from 'react-router-dom'

import { useTag } from '../../hooks/tags'
import { useLogin } from '../../hooks/login'

import PairwiseVote from './PairwiseVote'
import TagAccordion from './TagAccordion'

import Link from '@mui/material/Link'
import Card from '@mui/material/Card'
import Box from '@mui/material/Box'
import Typography from '@mui/material/Typography'

function TagTitle({ name, description, numItems, numVotes, numUsers, creator  }) {
  return <Card sx={{ padding: '1rem', margin: '1rem' }}>
           <Typography>{name}</Typography>
           <Typography>{numItems} items with {numVotes} vote{numVotes === 1 ? '' : 's'} from {numUsers} users</Typography>
           <Typography>{description}</Typography>
           <Typography component="div">
             Created by{' '}
             <Link component={LocalLink} to={`/users/${creator}`} >
               {creator}
             </Link>
           </Typography>
         </Card>
}

// main tag page
export default function Tag() {
  const { tagId } = useParams()
  const { loggedOut } = useLogin()
  const { tag, isLoading } = useTag(tagId)

  if (isLoading) return <div>loading</div>

  const { name, description, contributors, votes, items, creator } = tag

  const numItems = items.ranked.length + items.unranked.length

  return (
    <Box>
      <TagTitle
        name={name}
        description={description}
        numItems={numItems}
        numVotes={votes.length}
        numUsers={contributors.length}
        creator={creator}
      />

      {/* TODO: only show if user can vote */}
      {!loggedOut && <PairwiseVote />}
        {items.ranked && <TagAccordion
          title="Ranked Items"
          items={items.ranked}
          users={contributors}
        />}
        {items.unranked && <TagAccordion
          title="Unranked Items"
          items={items.unranked}
          users={contributors}
        />}
    </Box>
  )
}
