// get all the tags
function getTags() {
  return [
    {
      id: "a",
    },
    {
      id: "b"
    }
  ]
}

function getTagById(tagId) {
  return {
    name: tagId
  }
}

export { getTags, getTagById }
