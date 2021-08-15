package com.github.wirtzleg.scaling.utils;

import com.github.wirtzleg.scaling.dto.User;
import lombok.experimental.UtilityClass;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;

@UtilityClass
public class Utils {

    public static String chatId(long user1Id, long user2Id) {
        long minId = Math.min(user1Id, user2Id);
        long maxId = Math.max(user1Id, user2Id);

        return minId + "_" + maxId;
    }

    public static boolean isChatMember(long userId, String chatId) {
        String[] members = chatId.split("_");

        return Long.parseLong(members[0]) == userId || Long.parseLong(members[1]) == userId;
    }

    public static User user(Object obj) {
        if (obj instanceof User u)
            return u;

        if (obj instanceof UsernamePasswordAuthenticationToken token)
            return (User) token.getPrincipal();

        if (obj instanceof SecurityContext ctx) {
            if (ctx.getAuthentication() instanceof UsernamePasswordAuthenticationToken token)
                return (User) token.getPrincipal();
        }

        return null;
    }
}
