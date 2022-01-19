import { useCallback, useState } from 'react'

import { useNextVote } from '../../hooks/tags'

import Item from './Item'

import Button from '@mui/material/Button'
import Card from '@mui/material/Card'
import Slider from '@mui/material/Slider'
import Stack from '@mui/material/Stack'

const DEFAULT_RATING = 50

function PairwiseVote({ tagId }) {
  const { items, isLoading, submitVote, skipVote } = useNextVote(tagId)
  const [rating, setRating] = useState(DEFAULT_RATING) // state of the bar

  function onSlide(e, value) {
    setRating(e.target.value)
  }

  const submit = useCallback(() => {
    submitVote(rating)
    setRating(DEFAULT_RATING)
  }, [ rating, submitVote ])

  const skip = useCallback(() => {
      skipVote()
      setRating(DEFAULT_RATING)
  }, [ skipVote ])

  if (isLoading) return <div>Loading..</div>

  return <Card sx={{ padding: '1rem', margin: '1rem' }}>
          <Stack direction="row" justifyContent="space-between" spacing={2}>
            {items.map((item, i) => <Item key={i} {...item}/>)}
          </Stack>
          <Slider value={rating} step={10} marks min={0} max={100} onChange={onSlide} />
          <Stack direction="row" justifyContent="space-between" spacing={2}>
            <Button onClick={submit}>Vote</Button>
            <Button onClick={skip}>Skip</Button>
          </Stack>
        </Card>
}

export default PairwiseVote;
