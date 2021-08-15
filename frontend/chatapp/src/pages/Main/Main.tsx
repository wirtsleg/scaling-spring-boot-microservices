import React from 'react';

import { Chat, ContactList } from './components';
import { MainPageProvider } from './hooks';
import { StyledBox } from './Main.styled';

export const Main = () => (
  <MainPageProvider>
    <StyledBox>
      <ContactList />
      <Chat />
    </StyledBox>
  </MainPageProvider>
);
