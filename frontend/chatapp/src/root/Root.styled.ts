import { createGlobalStyle } from 'styled-components';

export const GlobalStyle = createGlobalStyle`
  *,
  *::before,
  *::after {
    box-sizing: border-box;
  }

  html,
  body,
  #root {
    height: 100%;
  }
  
  body {
    min-width: 500px;
    background: ${({ theme }) => theme.colors.light};
    font-family: Arial, sans-serif;
    font-size: 16px;
    color: ${({ theme }) => theme.colors.white};
  }
`;
