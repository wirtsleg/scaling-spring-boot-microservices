import styled from 'styled-components';

export const StyledContactList = styled.div`
  flex-shrink: 0;
  width: 270px;
  overflow: auto;
  background: ${({ theme }) => theme.colors.primary};
  user-select: none;
`;
