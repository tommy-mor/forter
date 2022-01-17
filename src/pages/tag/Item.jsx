import Typography from '@mui/material/Typography'

function Item({ type, name }) {
   return <Typography sx={{
        padding: '1rem',
        textAlign: 'center',
        textColor: "black",
    }}
    >{name}</Typography>
}

export default Item
