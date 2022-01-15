// get all the tags
async function getTags() {
  return new Promise(resolve => [{
    id: "a",
    name: "a"
  },
  {
    id: "b",
    name: "b"
  }
  ]).then(res => res)
}

function getTagById(tagId) {
  return new Promise(resolve => ({
    id: tagId,
    name: tagId,
    items: [
      {
        score: 1,
        votes: 3,
        name: "arst"
      },
    ],
  })).then(res => res)
}

export { getTags, getTagById }
