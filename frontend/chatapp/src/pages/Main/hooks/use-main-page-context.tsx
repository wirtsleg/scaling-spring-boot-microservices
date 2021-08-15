import { FC, createContext, useContext, useMemo, useState } from 'react';

import { User } from 'types';

type TMainPageContext = {
  activeContact: User | null;
  setActiveContact: (id: User | null) => void;
};

const MainPageContext = createContext<TMainPageContext>({
  activeContact: null,
  setActiveContact: () => {},
});

export const MainPageProvider: FC = ({ children }) => {
  const [activeContact, setActiveContact] = useState<User | null>(null);

  const ctx = useMemo(
    () => ({
      activeContact,
      setActiveContact,
    }),
    [activeContact]
  );

  return (
    <MainPageContext.Provider value={ctx}>{children}</MainPageContext.Provider>
  );
};

export const useMainPageContext = () => useContext(MainPageContext);
