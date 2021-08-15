import styled from 'styled-components';

import { AvatarSize } from './Avatar';

export const StyledAvatar = styled.div<{ size: AvatarSize }>`
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  width: ${({ size }) => (size === 'lg' ? 72 : 48)}px;
  height: ${({ size }) => (size === 'lg' ? 72 : 48)}px;
  background: white;
  border-radius: 50%;
  font-size: 20px;
  color: ${({ theme }) => theme.colors.black};
`;

export const StyledOnlineIndicator = styled.div<{ size: AvatarSize }>`
  position: absolute;
  right: ${({ size }) => (size === 'lg' ? 4 : 3)}px;
  bottom: ${({ size }) => (size === 'lg' ? 4 : 3)}px;
  width: ${({ size }) => (size === 'lg' ? 15 : 10)}px;
  height: ${({ size }) => (size === 'lg' ? 15 : 10)}px;
  border-radius: 50%;
  background-color: ${({ theme }) => theme.colors.green};
`;
