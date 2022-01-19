import { Suspense } from 'react'
import { Outlet } from 'react-router-dom'

import Box from '@mui/material/Box'
import Paper from '@mui/material/Paper'

export default function UserPage() {
  return (
    <Box>
      <Suspense fallback={<div>Loading tags...</div>}>
      <Paper sx={{ padding: '1em' }}>
        Users
        <Outlet />
      </Paper>
      </Suspense>
    </Box>
  )
}
