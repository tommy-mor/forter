import { useParams, Link as LocalLink } from 'react-router-dom'

import { useTag } from '../../hooks/tags'
import { useLogin } from '../../hooks/login'

import PairwiseVote from './PairwiseVote'
import TagAccordion from './TagAccordion'
import AddItemAccordion from './AddItemAccordion'

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
             <Link component={LocalLink} to={`/users/${creator.name}`} >
               {creator.name}
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

	console.log('tag', tag)

	const { tag: {description, title, creator},
			sorted,
			votelessitems,
			votes,
			users: {users, user} // all users, requested user
		  } = tag

  const numItems = sorted.length + votelessitems.length

  return (
    <Box>
      <TagTitle
        name={title}
        description={description}
        numItems={numItems}
        numVotes={votes.length}
        numUsers={users.length}
        creator={creator}
      />

      <AddItemAccordion settings={{}}/>

      {/* TODO: only show if user can vote */}
      {!loggedOut && <PairwiseVote />}
        {sorted && <TagAccordion
          title="Ranked Items"
          items={sorted}
          users={users}
        />}
        {votelessitems && <TagAccordion
          title="Unranked Items"
          items={votelessitems}
          users={users}
        />}
    </Box>
  )
}
