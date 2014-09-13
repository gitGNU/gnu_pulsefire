/*
 * Copyright (c) 2011, Willem Cazander
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *   following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and
 *   the following disclaimer in the documentation and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.nongnu.pulsefire.device.ui.tabs;

import org.nongnu.pulsefire.device.io.protocol.CommandName;

/**
 * JTabPanelPtt
 * 
 * @author Willem Cazander
 */
public class JTabPanelPtt extends AbstractFireTabPanel {
	
	public JTabPanelPtt() {
		build(
			createCompactGrid(3, 2, 
				createFlowLeftFirePanel("pttStatus",
					createLabeledGrid(2, 2, 
						createCommandStatusBoxLabelGrid(CommandName.ptt_fire,CommandName.ptt_idx,CommandName.ptt_cnt,0),
						createCommandStatusBoxLabelGrid(CommandName.ptt_fire,CommandName.ptt_idx,CommandName.ptt_cnt,1),
						createCommandStatusBoxLabelGrid(CommandName.ptt_fire,CommandName.ptt_idx,CommandName.ptt_cnt,2),
						createCommandStatusBoxLabelGrid(CommandName.ptt_fire,CommandName.ptt_idx,CommandName.ptt_cnt,3)
					)
				),
				createFlowLeftFirePanel("pttTrigger",
					createCommandButtonTrigger(CommandName.ptt_fire,0,CommandName.req_trigger),
					createCommandButtonTrigger(CommandName.ptt_fire,1,CommandName.req_trigger),
					createCommandButtonTrigger(CommandName.ptt_fire,2,CommandName.req_trigger),
					createCommandButtonTrigger(CommandName.ptt_fire,3,CommandName.req_trigger)
				),
				createCommandQMapTable(CommandName.ptt_0map),
				createCommandQMapTable(CommandName.ptt_1map),
				createCommandQMapTable(CommandName.ptt_2map),
				createCommandQMapTable(CommandName.ptt_3map)
			)
		);
	}
}
