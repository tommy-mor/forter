import { Home, Users, Login, Register } from './pages'
import { BrowserRouter, Routes, Route } from 'react-router-dom';

function Main() {

}

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Main/>}>
          <Route index element={<Home/>} />
          <Route path="users" element={<Users/>} >
            <Route path=":username" element={<User/>} />
            <Route
              index
              element={
                <main style={{ padding: "1rem" }}>
                  <p>Select a user</p>
                </main>
              }
            />
          </Route>
          <Route path="tags" element={<Tags/>}>
            <Route path=":tag_id" element={<Tag/>} />
            <Route
              index
              element={
                <main style={{ padding: "1rem" }}>
                  <p>Select a tag</p>
                </main>
              }
            />
          </Route>
          <Route path="login" element={<Login/>} />
          <Route path="register" element={<Register/>} />
          <Route
            path="*"
            element={
              <main style={{ padding: "1rem" }}>
                <p>Oops! Wrong page. Look elsewhere.</p>
              </main>
            }
          />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

export default App;
