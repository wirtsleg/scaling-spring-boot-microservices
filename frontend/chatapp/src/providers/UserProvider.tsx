import { FC, createContext, useContext, useState } from 'react';
import axios from 'axios';

import { User } from 'types';
import { useMount, useMountedState } from 'lib';

const UserContext = createContext<User | null>(null);

export const UserProvider: FC = ({ children }) => {
  const isMounted = useMountedState();
  const [user, setUser] = useState<User | null>(null);
  const [isError, setError] = useState(false);

  useMount(async () => {
    try {
      const { data } = await axios.get<User>('/api/v1/user');
      if (isMounted()) setUser(data);
    } catch (e) {
      if (isMounted()) setError(true);
    }
  });

  if (isError) return <div>Authentication error</div>;

  if (!user && !isError) return <div>Loading...</div>;

  return <UserContext.Provider value={user}>{children}</UserContext.Provider>;
};

export const useUserContext = () => useContext(UserContext) as User;
