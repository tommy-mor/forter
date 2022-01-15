import { fakePromise } from './utils'

function getTags() {
  return fakePromise([{
    id: "a",
    name: "a"
  },
  {
    id: "b",
    name: "b"
  }
  ])
}

function getTagById(tagId) {
  return fakePromise({
    id: tagId,
    name: tagId,
    items: [
      {
        score: 1,
        votes: 3,
        name: "arst"
      },
    ],
  })
}

export { getTags, getTagById }
