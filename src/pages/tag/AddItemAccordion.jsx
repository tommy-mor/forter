import { useForm } from 'react-hook-form'

import FormControl from '@mui/material/FormControl'
import Accordion from '@mui/material/Accordion'
import AccordionSummary from '@mui/material/AccordionSummary'
import AccordionDetails from '@mui/material/AccordionDetails'
import ExpandMoreIcon from '@mui/icons-material/ExpandMore'
import Stack from '@mui/material/Stack'
import Typography from '@mui/material/Typography'
import TextField from '@mui/material/TextField'
import Button from '@mui/material/Button'

function ItemForm({ settings, onSubmit }) {
    const { register, handleSubmit, formState: { errors } } = useForm()
    return <FormControl fullWidth>
        <Stack alignItems="center" spacing={1}>
            <TextField
                required
                error={!!errors.title}
                id="outlined-required"
                label="Title"
                {...register("title", { required: true })}
                />
            <TextField
                required
                error={!!errors.body}
                id="outlined-required"
                label="Body"
                {...register("body", { required: true })}
                />
            { onSubmit && <Button variant="contained" type="submit" onClick={handleSubmit(onSubmit)}>
            Add
            </Button>}
        </Stack>
    </FormControl>
}

function AddItemAccordion({ settings }) {
    return <Accordion sx={{ margin: '1rem' }}>
        <AccordionSummary
            expandIcon={<ExpandMoreIcon/>}
        >
            <Typography>Add Item</Typography>
        </AccordionSummary>
        <AccordionDetails>
            <ItemForm settings={settings} onSubmit={(d) => console.log(d)}/>
        </AccordionDetails>
    </Accordion>
}

export default AddItemAccordion
