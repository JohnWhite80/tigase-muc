/*
 * AbstractModule.java
 *
 * Tigase Jabber/XMPP Server
 * Copyright (C) 2004-2012 "Artur Hefczyc" <artur.hefczyc@tigase.org>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. Look for COPYING file in the top folder.
 * If not, see http://www.gnu.org/licenses/.
 *
 */

package tigase.muc.modules;

//~--- non-JDK imports --------------------------------------------------------

import java.util.Collection;
import java.util.logging.Logger;

import tigase.component.ElementWriter;
import tigase.component.exceptions.ComponentException;
import tigase.component.modules.Module;
import tigase.muc.MucConfig;
import tigase.muc.Room;
import tigase.muc.repository.IMucRepository;
import tigase.server.Iq;
import tigase.server.Message;
import tigase.server.Packet;
import tigase.util.TigaseStringprepException;
import tigase.xml.Element;
import tigase.xmpp.JID;
import tigase.xmpp.StanzaType;

//~--- JDK imports ------------------------------------------------------------

/**
 * @author bmalkow
 * 
 */
public abstract class AbstractModule implements Module {
	/**
	 * Method description
	 * 
	 * 
	 * @param iq
	 * 
	 * @return
	 */
	public static Element createResultIQ(Element iq) {
		return new Element(Iq.ELEM_NAME, new String[] { Packet.TYPE_ATT, Packet.FROM_ATT, Packet.TO_ATT, Packet.ID_ATT },
				new String[] { "result", iq.getAttributeStaticStr(Packet.TO_ATT), iq.getAttributeStaticStr(Packet.FROM_ATT),
						iq.getAttributeStaticStr(Packet.ID_ATT) });
	}

	/**
	 * Method description
	 * 
	 * 
	 * @param jid
	 * 
	 * @return
	 */
	public static String getNicknameFromJid(JID jid) {
		if (jid != null) {
			return jid.getResource();
		} else {
			return null;
		}
	}

	/** Field description */
	protected final MucConfig config;

	/** Field description */
	protected Logger log = Logger.getLogger(this.getClass().getName());

	// ~--- constructors
	// ---------------------------------------------------------

	/** Field description */
	protected final IMucRepository repository;

	// ~--- methods
	// --------------------------------------------------------------

	/** Field description */
	protected final ElementWriter writer;

	// ~--- get methods
	// ----------------------------------------------------------

	/**
	 * Constructs ...
	 * 
	 * 
	 * @param config
	 * @param writer
	 * @param mucRepository
	 */
	public AbstractModule(final MucConfig config, ElementWriter writer, final IMucRepository mucRepository) {
		this.config = config;
		this.repository = mucRepository;
		this.writer = writer;
	}

	// ~--- methods
	// --------------------------------------------------------------

	/**
	 * Method description
	 * 
	 * 
	 * @param room
	 * @param recipientNickame
	 * @param message
	 * 
	 * @throws TigaseStringprepException
	 */
	protected void sendMucMessage(Room room, String recipientNickame, String message) throws TigaseStringprepException {
		Collection<JID> occupantJids = room.getOccupantsJidsByNickname(recipientNickame);

		for (JID jid : occupantJids) {
			Packet msg = Message.getMessage(JID.jidInstance(room.getRoomJID()), jid, StanzaType.groupchat, message, null, null,
					null);
			writer.write(msg);
		}
	}

    @Override
    public void process(Packet packet) throws ComponentException, TigaseStringprepException {

    }
}

// ~ Formatted in Tigase Code Convention on 13/02/20