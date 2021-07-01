package com.couplecon.data;

public class JsonViews {
	
	public static class SearchParam {
		public static class Base {
			
		}
		
		public static class Range extends Base {
			
		}
		
		public static class Enumeration extends Base {
			
		}
		
		public static class Multiselect extends Enumeration {
			
		}
		
		public static class Singleselect extends Enumeration {
			
		}
	}
	
	public static class General {
		public static class Public {
			
		}
		
		public static class Matched extends Public{
			
		}
		
		public static class CoupleMember extends Matched {
			
		}
		
		public static class Private extends CoupleMember {
			
		}
		
		public static class Internal extends Private {
			
		}
	}
}
