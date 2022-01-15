async function fakePromise(data) {
  return new Promise(res => setTimeout(() => res(data), 100))
}

export { fakePromise }
