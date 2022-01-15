import { DataGrid } from '@mui/x-data-grid'

const columns = [
  { field: 'votes', headerName: 'Votes', width: 70 },
  { field: 'items', headerName: 'Items', width: 70 },
  { field: 'category', headerName: 'Category', width: 130 },
  { field: 'username', headerName: 'Username', width: 130 },
]

const rows = [
  { id: 0, votes: 100, items: 3, category: 'bananas', username: 'jake' },
  { id: 1, votes: 100, items: 3, category: 'bananas', username: 'tommy' },
  { id: 2, votes: 100, items: 3, category: 'bananas', username: 'creeper' },
]


export default function Home() {
  return (
    <div style={{ height: 400, width: '100%' }}>
      <DataGrid
        rows={rows}
        columns={columns}
        pageSize={5}
        rowsPerPageOptions={[5]}
        checkboxSelection
      />
    </div>
  )
}
