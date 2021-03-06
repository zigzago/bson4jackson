// Copyright 2010-2016 Michel Kraemer
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package de.undercouch.bson4jackson.deserializers;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;

import de.undercouch.bson4jackson.BsonConstants;
import de.undercouch.bson4jackson.BsonParser;
import de.undercouch.bson4jackson.types.JavaScript;

/**
 * Deserializes BSON JavaScript objects
 * @author Michel Kraemer
 * @since 2.8.0
 */
public class BsonJavaScriptDeserializer extends JsonDeserializer<JavaScript> {
	@Override
	@SuppressWarnings("deprecation")
	public JavaScript deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException {
		if (jp instanceof BsonParser) {
			BsonParser bsonParser = (BsonParser)jp;
			if (bsonParser.getCurrentToken() != JsonToken.VALUE_EMBEDDED_OBJECT ||
					(bsonParser.getCurrentBsonType() != BsonConstants.TYPE_JAVASCRIPT &&
					bsonParser.getCurrentBsonType() != BsonConstants.TYPE_JAVASCRIPT_WITH_SCOPE)) {
				throw ctxt.mappingException(JavaScript.class);
			}
			return (JavaScript)bsonParser.getEmbeddedObject();
		} else {
			TreeNode tree = jp.getCodec().readTree(jp);
			
			String code = null;
			TreeNode codeNode = tree.get("$code");
			if (codeNode instanceof ValueNode) {
				code = ((ValueNode)codeNode).asText();
			}
			
			Map<String, Object> scope = null;
			TreeNode scopeNode = tree.get("$scope");
			if (scopeNode instanceof ObjectNode) {
				@SuppressWarnings("unchecked")
				Map<String, Object> scope2 =
						jp.getCodec().treeToValue(scopeNode, Map.class);
				scope = scope2;
			}
			
			return new JavaScript(code, scope);
		}
	}
}
