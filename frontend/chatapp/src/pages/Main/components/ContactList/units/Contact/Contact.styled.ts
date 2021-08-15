import styled from 'styled-components';

import { Avatar } from 'components';

export const StyledContact = styled.div<{ isActive: boolean }>`
  display: flex;
  align-items: center;
  padding: 10px;
  background: ${({ theme, isActive }) =>
    isActive ? theme.colors.dark : 'transparent'};
  cursor: pointer;
`;

export const StyledAvatar = styled(Avatar)`
  margin-right: 10px;
`;

export const StyledName = styled.div`
  font-size: 20px;
`;
