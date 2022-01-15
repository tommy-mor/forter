// get all the tags
function getTags() {
  return [
    {
      id: "a",
      name: "a"
    },
    {
      id: "b",
      name: "b"
    }
  ]
}

function getTagById(tagId) {
  return {
    id: tagId,
    name: tagId,
    items: [
      {
        score: 1,
        votes: 3,
        name: "arst"
      },
    ],
  }
}

export { getTags, getTagById }
