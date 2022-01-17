import { Suspense } from 'react'
import { BrowserRouter, Routes, Route } from 'react-router-dom'
import { MainPage, Login, Register, DefaultPage } from './pages'
import { UserPage, Users, User } from './pages/user'
import { TagPage, Tags, Tag, NewTag } from './pages/tag'

function AppRoutes() {
  return (
      <Suspense fallback={<div>Loading...</div>}>
        <BrowserRouter>
        <Routes>
        <Route path="/" element={<MainPage />}>
          <Route index element={<Tags />} />
          <Route path="users" element={<UserPage />} >
            <Route path=":userId" element={<User />} />
            <Route index element={<Users />} />
          </Route>
          <Route path="tags" element={<TagPage />}>
            <Route path="new" element={<NewTag/>} />
            <Route path="view/:tagId" element={<Tag />} />
            <Route index element={<Tags />} />
          </Route>
          <Route path="login" element={<Login />} />
          <Route path="register" element={<Register />} />
          <Route path="*" element={<DefaultPage />} />
        </Route>
      </Routes>
    </BrowserRouter>
    </Suspense>
  )
}

export default AppRoutes
