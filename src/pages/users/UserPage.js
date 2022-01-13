import { Outlet } from 'react-router-dom';

export default function UserPage() {
  return (
    <main>
      <nav>you have used the users sub url
      </nav>
      <Outlet />
    </main>
  )
}
