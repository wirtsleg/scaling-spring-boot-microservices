import styled from 'styled-components';

export const StyledBox = styled.div`
  display: flex;
  align-items: center;
  padding: 10px;
`;

export const StyledInput = styled.input`
  width: 100%;
  padding: 10px;
  outline: none;
`;

export const StyledSendButton = styled.button`
  height: 100%;
  margin-left: 10px;
  background: ${({ theme }) => theme.colors.dark};
  border: none;
  font-size: 20px;
  color: ${({ theme }) => theme.colors.white};
  outline: none;
  cursor: pointer;
  transition-property: background-color, color;
  transition: ease-out 0.3s;

  &:hover {
    background: ${({ theme }) => theme.colors.white};
    color: ${({ theme }) => theme.colors.dark};
  }
`;
