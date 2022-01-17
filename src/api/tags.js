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
    description: "some tag description",
    creator: "a",
    votes: [
      {
        voter: "a",
        item: "arst",
        vs: "asdf",
        score: 1,
      }
    ],
    contributors: ["a", "b"],
    items: {
      ranked: [
        {
          score: 1,
          votes: 3,
          name: "arst",
          creator: "a"
        },
        {
          score: 1,
          votes: 3,
          name: "asdf",
          creator: "b"
        },
      ],
    unranked: [
      {
        score: 1,
        votes: 3,
        name: "poo",
        creator: "b"
      },
    ]}
  })
}

// get the two things to vote on next
function getNextVote(tagId) {
  return fakePromise([
    {
      type: "text",
      name: "item1"
    },
    {
      type: "text",
      name: "item2"
    }
  ])
}

export { getTags, getTagById, getNextVote }
