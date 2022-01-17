import Typography from '@mui/material/Typography'


function YoutubeEmbed({ title, embedId }) {
  return <div className="video-responsive">
    <iframe
      width="853"
      height="480"
      src={`https://www.youtube.com/embed/${embedId}`}
      frameBorder="0"
      allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
      allowFullScreen
      title="Embedded youtube"
    />
  </div>
};

function Image({ title, link }) {
    <img
      src={link}
      alt={title}
    />
}

function Text({name}) {
    return <Typography sx={{
        padding: '1rem',
        textAlign: 'center',
        textColor: "black",
    }}
    >{name}</Typography>
}

function Default() {
    return <div>None</div>
}

function Item(props) {
   switch(props.type) {
        case 'text': return <Text {...props}/>
        case 'youtube': return <YoutubeEmbed {...props}/>
        case 'image': return <Image {...props}/>
        default: return  <Default {...props} />
    }
}

export default Item
