import { Reset } from 'styled-reset';

import { Main } from 'pages';
import { StompClientProvider, ThemeProvider, UserProvider } from 'providers';

import { GlobalStyle } from './Root.styled';

export const Root = () => (
  <ThemeProvider>
    <Reset />
    <GlobalStyle />
    <UserProvider>
      <StompClientProvider>
        <Main />
      </StompClientProvider>
    </UserProvider>
  </ThemeProvider>
);
