import { BrowserRouter, Routes, Route } from 'react-router-dom'
import MainPage from './pages/MainPage.js'
import { Home, Login, Register, DefaultPage } from './pages'
import { UserPage, Users, User } from './pages/users'
import { TagPage, Tags, Tag } from './pages/tags'

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<MainPage/>}>
          <Route index element={<Home/>} />
          <Route path="users" element={<UserPage/>} >
            <Route path=":username" element={<User/>} />
            <Route index element={<Users/>} />
          </Route>
          <Route path="tags" element={<TagPage/>}>
            <Route path=":tagId" element={<Tag/>} />
            <Route index element={<Tags />} />
          </Route>
          <Route path="login" element={<Login/>} />
          <Route path="register" element={<Register/>} />
          <Route path="*" element={<DefaultPage/>} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

export default App;
