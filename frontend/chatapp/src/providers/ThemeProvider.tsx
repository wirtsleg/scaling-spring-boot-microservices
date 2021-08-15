import { FC } from 'react';
import { ThemeProvider as StyledThemeProvider } from 'styled-components';

export const theme = {
  colors: {
    primary: '#37474f',
    light: '#62727b',
    dark: '#102027',
    white: '#ffffff',
    black: '#000000',
    green: '#81c784',
  },
};

export const ThemeProvider: FC = ({ children }) => (
  <StyledThemeProvider {...{ theme }}>{children}</StyledThemeProvider>
);
