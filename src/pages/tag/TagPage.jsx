import { Suspense } from 'react'
import { Outlet } from 'react-router-dom'

import Box from '@mui/material/Box'
import Paper from '@mui/material/Paper'

export default function TagPage() {
  return (
      <Suspense fallback={<div>Loading tags...</div>}>
        <Box>
          <Paper sx={{ padding: '1em' }}>
            Tags
            <Outlet />
          </Paper>
        </Box>
</Suspense>
  )
}
