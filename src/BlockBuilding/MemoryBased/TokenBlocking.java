/*
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    Copyright (C) 2015 George Antony Papadakis (gpapadis@yahoo.gr)
 */

package BlockBuilding.MemoryBased;

import BlockBuilding.AbstractTokenBlocking;
import DataStructures.EntityProfile;
import java.util.List;

/**
 *
 * @author gap2
 */
public class TokenBlocking extends AbstractTokenBlocking {

    public TokenBlocking(List<EntityProfile>[] profiles) {
        super(profiles);
    }
    
    @Override
    protected void setDirectory() {
        setMemoryDirectory();
    }
}
